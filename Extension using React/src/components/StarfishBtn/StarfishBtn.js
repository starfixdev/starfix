import React from 'react';
import { Button } from 'react-bootstrap';

const starfishBtn = (props) => {
	return (
		<>
			<Button variant='primary' size={props.size} onClick={props.clicked}>
				{props.text}
			</Button>
		</>
	);
};

export default starfishBtn;
