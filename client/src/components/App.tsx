import React from 'react';
import logo from './logo.svg';
import './App.css';

function App() {
	return (
		<div className="app">
			<header className="app-header">
				<h1 className="app-name" translate="no">MyloBot</h1>
			</header>
			<main className="content">
				<div className="song-queue">
					<div className="now-playing">
						Now Playing: some song
					</div>
					<ul className="upcoming-songs">
						<li>Song 1</li>
						<li>Song 2</li>
						<li>Song 3</li>
					</ul>
				</div>
				<div className="controls">
					<button className="pause-play">||</button>
					<button className="skip">&gt;</button>
				</div>
				<img src={logo} className="App-logo" alt="logo" />
			</main>
		</div>
	);
}

export default App;
