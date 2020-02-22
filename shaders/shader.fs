#version 330

// A sampler is the location of the texture
uniform sampler2D sampler;

in vec2 tex_coords;


void main() {
    // Setting the colour of the rendered rectangle
    gl_FragColor = texture2D(sampler, tex_coords);
}