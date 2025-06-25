import { createSlice } from '@reduxjs/toolkit';

const initialState = { nickname : null, userId : null, accessToken : null };

const authSlice = createSlice(
{
    name : 'auth',
    initialState,
    reducers : 
    {
        setUserInfo : (state, action) =>
        {
            const { nickname, userId, accessToken } = action.payload;
            state.nickname = nickname;
            state.userId = userId;
            state.accessToken = accessToken;
        },

        clearUserInfo : (state) =>
        {
            state.nickname = null;
            state.userId = null;
            state.accessToken = null;
        }
    },
});

export const { setUserInfo, clearUserInfo } = authSlice.actions;
export default authSlice.reducer