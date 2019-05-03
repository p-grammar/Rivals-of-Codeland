#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 iTexCoords;

uniform mat4 mvp;
uniform vec4 frame;
uniform float invert;

out vec2 texCoords;
out vec2 texCoords2;

out float add;
out float multiply;

void main() {
	texCoords  = iTexCoords;
	texCoords2 = iTexCoords * frame.xy + frame.zw;

    gl_Position = mvp*vec4(vertices, 1);

    if(invert > 0) {
        add = 1;
        multiply = -1;
    } else {
        add = 0;
        multiply = 1;
    }
}
