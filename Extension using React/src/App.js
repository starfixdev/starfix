import React, { Component } from 'react';
import './App.css';
import StarfishDropdown from './components/StarfishDropdown/StarfishDropdown';

class App extends Component {
	state = {
		currentIde: 'VSCode',
	};

	componentDidMount() {
		const currentIde = localStorage.getItem('currentIde');
		if (currentIde) {
			this.setState({ selectedIde: currentIde });
		}
	}

	ideSelectedHandler = (event) => {
		const val = event.target.id;
		let selectedIde = '';
		if (val === 'code') selectedIde = 'VSCode';
		else if (val === 'eclipse') selectedIde = 'Eclipse';
		else if (val === 'intellij') selectedIde = 'IntelliJ';
		this.setState({ currentIde: selectedIde });
		localStorage.setItem('currentIde', selectedIde);
	};

	render() {
		return (
			<div className='App'>
				<p>
					Current ide: <b>{this.state.currentIde}</b>
				</p>
				<StarfishDropdown clicked={this.ideSelectedHandler} />
			</div>
		);
	}
}

export default App;
