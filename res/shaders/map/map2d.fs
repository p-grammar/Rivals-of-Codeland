#version 330 core

uniform sampler2D sampler;
uniform sampler2D sampler2;

uniform vec3 inColor;

in vec2 texCoords;
in vec2 texCoords2;

in float add;
in float multiply;

out vec4 color;

void main(){
    float part = texture(sampler, texCoords).x * multiply + add;
    color = texture(sampler2, texCoords2) * vec4(inColor.xyz, part);
}
