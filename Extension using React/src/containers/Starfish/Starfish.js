import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import StarfishBtn from '../../components/StarfishBtn/StarfishBtn';

class StarfishContainer extends Component {
	render() {
		return (
			<div>
				<StarfishBtn text='Starfish'></StarfishBtn>
			</div>
		);
	}
}
const app = document.createElement('div');
const navigation = document.getElementsByClassName('file-navigation');
navigation[0].appendChild(app);
ReactDOM.render(<StarfishContainer />, app);
