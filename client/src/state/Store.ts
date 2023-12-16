import { configureStore } from '@reduxjs/toolkit';
import { IAppState } from '../models/State';
import { ActionTypes, IAction } from './Actions';

const INITIAL_STATE: IAppState = {
	userSnowflake: null,
	queue: [],
	isPaused: false
};

const reducer = function (state: IAppState = INITIAL_STATE, action: IAction): IAppState {
	switch (action.type) {
		case ActionTypes.APPEND_SONG:
			return {
				...state,
				queue: state.queue.concat(action.payload)
			};
		case ActionTypes.PREPEND_SONG:
			return {
				...state,
				queue: [action.payload, ...state.queue]
			};
		case ActionTypes.PAUSE_SONG:
			return {
				...state,
				isPaused: true
			};
		case ActionTypes.UNPAUSE_SONG:
			return {
				...state,
				isPaused: false
			};
		case ActionTypes.SKIP:
			return {
				...state,
				queue: state.queue.slice(action.payload)
			};
		case ActionTypes.CLEAR_SONGS:
			return {
				...state,
				queue: []
			};
	}

	return state;
}

export const store = configureStore({
	reducer: reducer,
	middleware: (getDefaultMiddleware) => getDefaultMiddleware()
});

// Infer the `RootState` and `AppDispatch` types from the store itself
// export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
