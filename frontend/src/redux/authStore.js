// store.js
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice';  // 경로 맞게 수정

const authStore = configureStore({
  reducer: {
    auth: authReducer,
  },
});

export default authStore;
