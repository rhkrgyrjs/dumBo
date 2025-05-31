import { createSlice } from '@reduxjs/toolkit';

const initialState = { nickname : null, username : null, accessToken : null };

const authSlice = createSlice(
{
    name : 'auth',
    initialState,
    reducers : 
    {
        setUserInfo : (state, action) =>
        {
            const { nickname, username, accessToken } = action.payload;
            state.nickname = nickname;
            state.username = username;
            state.accessToken = accessToken;
        },

        clearUserInfo : (state) =>
        {
            state.nickname = null;
            state.username = null;
            state.accessToken = null;
        }
    },
});

export const { setUserInfo, clearUserInfo } = authSlice.actions;
export default authSlice.reducer