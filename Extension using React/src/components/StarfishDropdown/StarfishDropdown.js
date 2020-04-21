import React from 'react';

import './StarfishDropdown.css';

const starfishDropdown = (props) => {
	return (
		<div className="Dropdown">
			<button className="Dropbtn">Choose an ide</button>
			<div className="DropdownContent">
				<a onClick={props.clicked} id="code">VSCode</a>
				<a onClick={props.clicked} id="eclipse">Eclipse</a>
				<a onClick={props.clicked} id="intellij">IntelliJ</a>
			</div>
		</div>
	);
};

export default starfishDropdown;
