#version 330 core

uniform sampler2D sampler;
uniform vec3 inColor;

in vec2 texCoords;

out vec4 color;

void main(){
    float modif0 = texture(sampler, texCoords + vec2(0.01, 0)).x;
    modif0 += texture(sampler, texCoords + vec2(0, 0.08)).x;
    modif0 += texture(sampler, texCoords + vec2(-0.01, 0)).x;
    modif0 += texture(sampler, texCoords + vec2(0, -0.08)).x;

    modif0 += texture(sampler, texCoords + vec2(0.005, -0.04)).x;
    modif0 += texture(sampler, texCoords + vec2(-0.005, 0.04)).x;
    modif0 += texture(sampler, texCoords + vec2(0.005, 0.04)).x;
    modif0 += texture(sampler, texCoords + vec2(-0.005, -0.04)).x;

    modif0 /= 8;

    modif0 = 1 - modif0;

    color = vec4(inColor.x + modif0, inColor.yz, texture(sampler, texCoords).x);
}