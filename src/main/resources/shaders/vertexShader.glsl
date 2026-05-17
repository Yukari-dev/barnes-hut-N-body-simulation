#version 330 core
layout(location = 0) in vec3 position;
layout(location = 1) in vec3 speedColor;
layout(location = 2) in vec3 starColor;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

out vec3 fragColor;

void main() {
    fragColor = starColor;
    gl_PointSize = 2.0;
    gl_Position  = projection * view * model * vec4(position, 1.0);
}
