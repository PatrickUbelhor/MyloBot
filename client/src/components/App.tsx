import React from 'react';
import logo from './logo.svg';
import './App.scss';
import NowPlaying from './now-playing/NowPlaying';

function App() {
	return (
		<div className="app">
			<header className="app-header">
				<h1 className="app-name" translate="no">MyloBot</h1>
			</header>
			<main className="content">
				<div className="song-queue">
					<NowPlaying />
					<div className="controls">
						<button className="pause-play">||</button>
						<button className="skip">&gt;</button>
					</div>
					<h2>Queue</h2>
					<ol className="upcoming-songs">
						<li>Dark Elves City | Menzoberranzan | ASMR Ambience | 1 Hour</li>
						<li>Combat Music Megamix - The Witcher 3: Wild Hunt</li>
						<li>The Infinite Library📜✨ [ Immersive Ambience Experience ]</li>
					</ol>
				</div>
				<img src={logo} className="App-logo" alt="logo" />
			</main>
		</div>
	);
}

export default App;
