
export interface IAction {
	type: ActionTypes;
	payload?: any;
}

export enum ActionTypes {
	APPEND_SONG = '[QUEUE] append',
	PREPEND_SONG = '[QUEUE] prepend',
	PAUSE_SONG = '[QUEUE] pause',
	UNPAUSE_SONG = '[QUEUE] unpause',
	SKIP = '[QUEUE] skip',
	CLEAR_SONGS = '[QUEUE] clear'
}
