import { createSlice } from "@reduxjs/toolkit";
import fadeBackground from "../components/modals/FadeBackground";

const initialState = 
{
    modals : {},     // 등록된 모달들
    modalStack : [], // 모달의 이름만 저장
    fadeBackground : false // 열린 페이지가 흐린 화면 처리
};

const modalStackSlice = createSlice(
    {
        name : 'modalStack',
        initialState,
        reducers :
        {
            // 모달을 사용하려면, 등록해야 함
            registerModal : (state, action) =>
            {
                const { modalName, modalClear, setModalShow, setModalInfo } = action.payload;
                if (!state.modals[modalName]) state.modals[modalName] = { 'modalName' : modalName, 'modalClear' : modalClear, 'setModalShow' : setModalShow, 'setModalInfo' : setModalInfo };
            },

            // 모달을 띄워줌
            showModal : (state, action) =>
            {
                const modalName = action.payload;
                if (!state.modals[modalName]) throw new Error(`등록되지 않은 모달 '${modalName}'`);
                
                // 스택에 해당 모달이 있다면 제거(중복 방지 : 뒤로가기시 같은 모달이 나오는 경우)
                state.modalStack = state.modalStack.filter( (modal) => modal.modalName !== modalName);

                // 만약 현재 열린 모달을 제외하고, 이전에 열린 모달(들)이 존재할 경우, z-index 처리와 fade 처리
                for (let i=0; i < state.modalStack.length; i++) state.modalStack[i].setModalInfo({ 'fade' : true, 'z' : (i+3)*10 });

                // 스택에 모달 푸시
                state.modalStack.push(state.modals[modalName]);

                // 모달 z-index 처리와 fade 처리
                state.modalStack[state.modalStack.length-1].setModalInfo({ 'fade' : false, 'z' : (state.modalStack.length+1)*10 })

                // 뒷배경 블러처리
                state.fadeBackground = true;

                // 모달 보이게 하기
                state.modalStack[state.modalStack.length-1].setModalShow(true);
            },

            // 가장 나중에 뜬 모달을 닫아줌
            popModal : (state) =>
            {
                // 모달이 없는데 닫으려 하는 경우는 처리하지 않기
                if (state.modalStack.length == 0) return;

                // 맨 위 모달을 pop
                let topModal = state.modalStack.pop();

                // 맨 위 모달을 안 보이게 하기
                topModal.setModalShow(false);

                // 맨 위 모달에 들어있는 정보 초기화시키기
                topModal.modalClear();
                
                // 만약 열려 있던 모달이 있다면, 활성화시키기
                if (state.modalStack.length > 0) state.modalStack[state.modalStack.length-1].setModalInfo({ 'fade' : false, 'z' : (state.modalStack.length+1)*10 });
                // 만약 열려 있는 모달이 없을 경우, 뒷배경 블러처리 해제
                else state.fadeBackground = false;

            }
        },
    }
);

export const { registerModal, showModal, popModal } = modalStackSlice.actions;
export default modalStackSlice.reducer;