#version 330 core

layout (location = 0) in vec3 vertices;

uniform mat4 mvp;
uniform vec4 color;
uniform vec2[3] offset;

out vec4 outColor;

void main() {
	outColor = color;
	vec2 off = offset[gl_VertexID];
    gl_Position = mvp*vec4(vertices + vec3(off.x, off.y, 1), 1);
    
}