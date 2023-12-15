import React from 'react';
import './NowPlaying.scss';

export default function NowPlaying() {
	return (
		<div className="now-playing">
			<h2>Now Playing:</h2>
			<div className="now-playing__song">
				<span className="now-playing__album-icon"></span>
				<div className="now-playing__song-info">
					<span className="song-title">Song Title</span>
					<span className="artist-name">Artist Name</span>
					<span className="requester-name">Requester name</span>
				</div>
			</div>
		</div>
	);
}
