#version 330 core

layout (location = 0) in vec3 vertices;

uniform mat4 mvp;
uniform vec4 color;
uniform vec4 color2;

out vec4 outColor;

void main() {
	outColor = (color * vertices.y) + (color2 * (1-vertices.y));
    gl_Position = mvp*vec4(vertices, 1);
}
