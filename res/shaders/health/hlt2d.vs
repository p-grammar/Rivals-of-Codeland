#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 iTexCoords;

uniform mat4 mvp;

uniform mat4 model;
uniform vec4 plane;

out vec2 texCoords;

void main() {
	texCoords = iTexCoords;
    gl_Position = mvp*vec4(vertices, 1);

    gl_ClipDistance[0] = dot(plane, model*vec4(vertices, 1));
}