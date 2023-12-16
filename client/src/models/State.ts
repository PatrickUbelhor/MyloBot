
export interface IAppState {
	userSnowflake: number;
	queue: ISongInfo[];
	isPaused: boolean;
}

export interface ISongInfo {
	name: string;
	artist: string;
	requester: string;
	duration: number;
	albumArtworkUrl: string;
}
