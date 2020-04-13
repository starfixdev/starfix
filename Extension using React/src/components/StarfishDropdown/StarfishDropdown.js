import React from 'react';
import { Dropdown } from 'react-bootstrap';

const starfishDropdown = (props) => {
	return (
		<Dropdown>
			<Dropdown.Toggle variant='primary' size={props.size}>Select an IDE</Dropdown.Toggle>
			<Dropdown.Menu>
				<Dropdown.Item id="code" onClick={props.clicked}>VSCode</Dropdown.Item>
				<Dropdown.Item id="eclipse" onClick={props.clicked}>Eclipse</Dropdown.Item>
				<Dropdown.Item id="intellij" onClick={props.clicked}>IntelliJ</Dropdown.Item>
			</Dropdown.Menu>
		</Dropdown>
	);
};

export default starfishDropdown;
