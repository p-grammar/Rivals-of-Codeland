#version 330 core

in vec4 outColor;
in vec2 pos;

out vec4 color;

void main() {
    if( pow(pos.x - 0.5, 2) + pow(pos.y - 0.5, 2) < 0.25) {
	    color = outColor * vec4(1, 1, 1, 1);
	} else {
	    color = outColor * vec4(1, 1, 1, 0);
	}
}
