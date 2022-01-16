#version 430
void main(void)
{
float rad = 0.0;
mat4 xrot = mat4( 1.0, 0.0, 0.0, 0.0,
0.0, cos(rad), -sin(rad), 0.0,
0.0, sin(rad), cos(rad), 0.0,
0.0, 0.0, 0.0, 1.0 );


if (gl_VertexID == 0) gl_Position = xrot * vec4(0.25, -0.25, 0.0, 1.0);
else if (gl_VertexID == 1) gl_Position = xrot * vec4(-0.25, -0.25, 0.0, 1.0);
else gl_Position = xrot * vec4(0.25, 0.25, 0.0, 1.0);
}
