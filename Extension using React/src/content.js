import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import StarfishBtn from './components/StarfishBtn/StarfishBtn';
import StarfishDropdown from './components/StarfishDropdown/StarfishDropdown';

import classes from './content.css';

window.browser = (function () {
	return window.msBrowser || window.browser || window.chrome;
})();

class StarfishContainer extends Component {
	state = {
		ide: {
			vscode: 'code',
			eclipse: 'eclipse',
			intellij: 'intellij',
		},
		url: '',
		selectedIde: '',
		pageUrl: window.location,
		showDropDown: false,
	};

	dropdownDisplayHandler = () => {
		console.log('Show Dropdown');
		this.setState((prevState) => ({
			showDropDown: !prevState.showDropDown,
		}));
	};

	cloneRepositoryClickedHandler = () => {
		if (this.state.pageUrl !== '') {
			if (this.state.selectedIde !== '') {
				const url =
					this.state.selectedIde +
					'://clone-url?url=' +
					this.state.pageUrl;
				this.setState({ url: url });
			} else {
				alert('Select an IDE');
			}
		}
	};

	dropdownSelectedHandler = (event) => {
		let ide = event.target.id;
		console.log(ide);
		this.setState({ selectedIde: ide });
	};

	render() {
		let dropdown = null;
		if (this.state.showDropDown) {
			dropdown = (
				<div className={classes.dropdownDiv}>
					<StarfishBtn
						size='sm'
						text='Clone this repository'
						clicked={
							this.cloneRepositoryClickedHandler
						}></StarfishBtn>
					<StarfishDropdown
						ide={this.state.ide}
						size='sm'
						clicked={
							this.dropdownSelectedHandler
						}></StarfishDropdown>
				</div>
			);
		}
		return (
			<div>
				<StarfishBtn
					size='sm'
					text='Starfish'
					clicked={this.dropdownDisplayHandler}></StarfishBtn>
				{dropdown}
				<p>{this.state.url}</p>
			</div>
		);
	}
}

const app = document.createElement('div');
app.id = 'myExtensionRoot';
app.classList.add('starfishContainer');
const navigation = document.getElementsByClassName('file-navigation');
navigation[0].appendChild(app);
ReactDOM.render(<StarfishContainer />, app);
