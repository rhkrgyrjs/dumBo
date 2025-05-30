import { createSlice } from '@reduxjs/toolkit';

const initialState = 
{
  registeredModals: {},   // name -> { modalClear }
  modalStack: [],         // [{ name }]
};

const modalSlice = createSlice(
{
  name: 'modal',
  initialState,
  reducers: 
  {
    registerModal: (state, action) => 
    {
      const { name, modalClear } = action.payload;
      if (!name || !modalClear) throw new Error('registerModal: name 또는 modalClear 누락');
      if (state.registeredModals[name]) throw new Error(`Modal '${name}'은 이미 등록됨`);
      state.registeredModals[name] = { modalClear };
    },

    pushModal: (state, action) => 
    {
      const name = action.payload;
      if (!state.registeredModals[name]) throw new Error(`등록되지 않은 모달 '${name}'`);
      // 중복 제거
      state.modalStack = state.modalStack.filter(modal => modal.name !== name);
      state.modalStack.push({ name });
    },

    popModal: (state) => 
    {
      const popped = state.modalStack.pop();
      if (popped && state.registeredModals[popped.name]) { state.registeredModals[popped.name].modalClear(); }
    },
  },
});

export const { registerModal, pushModal, popModal } = modalSlice.actions;
export default modalSlice.reducer;
