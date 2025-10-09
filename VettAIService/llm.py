from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder, FewShotChatMessagePromptTemplate
from langchain.chains import create_history_aware_retriever, create_retrieval_chain
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain_upstage import ChatUpstage
from langchain_upstage import UpstageEmbeddings
from langchain_pinecone import PineconeVectorStore
from langchain_community.chat_message_histories import ChatMessageHistory
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.runnables.history import RunnableWithMessageHistory
from config import answer_examples
from mongoDBClient import MongoDBChatMessageHistory
import textwrap
import os
from dotenv import load_dotenv

load_dotenv()


def get_session_history(session_id: str) -> BaseChatMessageHistory:
    return MongoDBChatMessageHistory(session_id)

def get_retriever():
    embedding = UpstageEmbeddings(model="embedding-query")
    index_name = os.getenv("PINECONE_INDEX_NAME")
    database = PineconeVectorStore.from_existing_index(index_name=index_name, embedding=embedding)
    retriever = database.as_retriever(search_kwargs={'k': 4})
    return retriever


def get_llm(model='solar-pro'):
    llm = ChatUpstage(model=model,api_key=os.getenv("UPSTAGE_API_KEY"))
    return llm

def get_history_retriever():
    llm = get_llm()
    retriever = get_retriever()
    
    contextualize_q_system_prompt = (
        "Given a chat history and the latest user question, "
        "which might reference context in the chat history, "
        "formulate a standalone question which can be understood without the chat history. "
        "Do NOT answer the question, just reformulate it if needed and otherwise return it as is. "
        "Additionally, insert natural line breaks at appropriate places such as after periods (.), dashes (-), "
        "or other punctuation marks where it improves readability."
        "Use '\\n' to indicate line breaks in the output text."
    )

    contextualize_q_prompt = ChatPromptTemplate.from_messages(
        [
            ("system", contextualize_q_system_prompt),
            MessagesPlaceholder("chat_history"),
            ("human", "{input}"),
        ]
    )
    
    history_aware_retriever = create_history_aware_retriever(
        llm, retriever, contextualize_q_prompt
    )
    return history_aware_retriever

def get_dictionary_chain():
    dictionary = []
    llm = get_llm()
    prompt = ChatPromptTemplate.from_template(
        textwrap.dedent(f"""
            사용자의 질문을 보고, 우리의 사전을 참고해서 사용자의 질문을 변경해주세요.
            만약 변경할 필요가 없다고 판단된다면, 사용자의 질문을 변경하지 않아도 됩니다.
            그런 경우에는 질문만 리턴해주세요
            사전: {dictionary}
            질문: {{question}}
        """)
    )
    dictionary_chain = prompt | llm | StrOutputParser()
    return dictionary_chain

def get_rag_chain():
    llm = get_llm()
    example_prompt = ChatPromptTemplate.from_messages(
        [
            ("human", "{input}"),
            ("ai", "{answer}"),
        ]
    )
    few_shot_prompt = FewShotChatMessagePromptTemplate(
        example_prompt=example_prompt,
        examples=answer_examples,
    )

    system_prompt = (
        "당신은 반려동물 건강 전문가입니다. 사용자의 반려동물 건강에 관한 질문에 답변해주세요."
        "아래에 제공된 문서를 활용해서 답변해주시고,"
        "답변을 알 수 없다면 모른다고 답변해주세요."
        "답변을 제공할 때는 '출처에 따르면' 이라고 시작하면서 답변해주시고,"
        "4 ~ 5 문장 정도의 내용의 답변을 원합니다."
        "\n\n"
        "{context}"
    )
    
    qa_prompt = ChatPromptTemplate.from_messages(
        [
            ("system", system_prompt),
            few_shot_prompt,
            MessagesPlaceholder("chat_history"),
            ("human", "{input}"),
        ]
    )
    history_aware_retriever = get_history_retriever()
    question_answer_chain = create_stuff_documents_chain(llm, qa_prompt)

    rag_chain = create_retrieval_chain(history_aware_retriever, question_answer_chain)
    
    conversational_rag_chain = RunnableWithMessageHistory(
        rag_chain,
        get_session_history,
        input_messages_key="input",
        history_messages_key="chat_history",
        output_messages_key="answer",
    ).pick('answer')
    
    return conversational_rag_chain


def get_diagnosis_from_llm(disease: str, confidence: float, description: str):

    request_message = f"""
        당신은 반려동물 건강 진단 전문가입니다. 사용자는 '{disease}'라는 질병이 감지되었고, 신뢰도는 {confidence * 100}%입니다.
        다음은 반려동물의 환경 및 증상 설명입니다: "{description}"

        위 정보를 기반으로 아래와 같이 응답해주세요:
        1. 이 반려동물에게 '{disease}' 질병이 {confidence}% 신뢰도로 발생했을 가능성이 높습니다.
        2. 해당 질병에 대한 짧은 요약 설명 (1~2문장)
        3. 현재 증상과 환경을 고려했을 때의 예상 원인
        4. 집에서 시도해볼 수 있는 초기 조치 (가능한 경우)
        5. 반드시 병원에 방문해야 하는지 여부에 대한 전문가의 조언

        ※ 추정이 아닌 가능성에 기반한 설명을 해주세요.
        ※ 문장은 사용자에게 직접 설명하듯이 친절하게 작성해주세요. 그리고 번호는 붙이지 마세요.
            """.strip()

    severity_prompt = f"""
        당신은 반려동물 질병 진단 평가 전문가입니다. 질병 이름은 '{disease}'이며, 신뢰도는 {confidence}%입니다.
        반려동물의 환경 및 상태는 다음과 같습니다: "{description}"
        위 정보를 바탕으로 다음 5단계 중 하나로 위험 수준을 진단해주세요:
        [매우 높음, 높음, 보통, 낮음, 매우 낮음]

        ※ 반드시 아래 형식 중 **하나만** content로 반환해주세요.
        ※ 신뢰도가 높다면 되도록이면 보통 이상으로 진단해주세요.
        예: "높음"
        ※ 부가설명 없이 오직 하나의 단어만 포함되게 해주세요.
    """.strip()

    llm = get_llm()

    narrative_resp = llm.invoke(request_message)
    narrative_text = getattr(narrative_resp, "content", "").strip()

    severity_resp = llm.invoke(severity_prompt)
    severity_text = getattr(severity_resp, "content", "").strip()

    valid_levels = ["매우 높음", "높음", "보통", "낮음", "매우 낮음"]
    severity_level = next((lvl for lvl in valid_levels if lvl == severity_text), None)

    if not narrative_text:
        narrative_text = f"해당 반려동물에서 '{disease}' 징후가 {conf_percent}% 신뢰도로 관찰되었습니다. " \
                         f"정확한 진단을 위해 가까운 동물병원의 진료를 권장드립니다."

    return {
        "diagnosisResult": narrative_text,
        "severityLevel": severity_level,
    }