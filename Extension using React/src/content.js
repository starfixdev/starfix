import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import StarfishBtn from './components/StarfishBtn/StarfishBtn';

window.browser = (function () {
	return window.msBrowser || window.browser || window.chrome;
})();

class StarfishContainer extends Component {
	state = {
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

	render() {
		let dropdown = null;
		if (this.state.showDropDown) {
			dropdown = (
				<div className='position-relative'>
					<div className='get-repo-modal dropdown-menu dropdown-menu-sw pb-0 js-toggler-container js-get-repo-modal'>
						<div className='get-repo-model-options'>
							<StarfishBtn
								text='Clone Repository using IDE'
								size='sm'
								clicked={this.cloneRepositoryClickedHandler}
							/>
							<br></br>
						</div>
					</div>
				</div>
			);
		}
		return (
			<div>
				{/* <StarfishBtn
					size='sm'
					text='Starfish'
					clicked={this.dropdownDisplayHandler}></StarfishBtn>
				{dropdown}
				<p>{this.state.url}</p> */}
				<span className='d-flex'>
					<details className='get-repo-select-menu js-get-repo-select-menu position-relative details-overlay details-reset'>
						<summary
							className='btn btn-sm ml-2 btn-primary'
							onClick={this.dropdownDisplayHandler}>
							Starfish
						</summary>
						{dropdown}
					</details>
				</span>
			</div>
		);
	}
}

const app = document.createElement('div');
app.id = 'myExtensionRoot';
const navigation = document.getElementsByClassName(
	'file-navigation in-mid-page mb-2 d-flex flex-items-start'
);
navigation[0].appendChild(app);
ReactDOM.render(<StarfishContainer />, app);
