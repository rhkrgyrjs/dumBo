import { configureStore, getDefaultMiddleware } from '@reduxjs/toolkit';
import authReducer from './authSlice';
import modalStack from './modalStackSlice';

export const store = configureStore(
{
    reducer : 
    {
        auth: authReducer,
        modal: modalStack,
    },

    middleware : (middleware) => middleware({ serializableCheck: false, }), // serializable 경고 안 뜨게 하기 위해
});
