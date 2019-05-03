#version 330 core

uniform vec4 inColor;

uniform sampler2D sampler;

in vec2 texCoords;
in vec4 outColor;

out vec4 color;

void main(){
	color = texture(sampler, texCoords);
	if(color.x < 0.5) {
	    color.w = 0;
	} else {
	    color.w = outColor.w;
	}
	color.xyz = outColor.xyz;
}