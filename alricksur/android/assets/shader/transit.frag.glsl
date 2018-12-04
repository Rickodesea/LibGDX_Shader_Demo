#ifdef GL_ES
precision mediump float;
#endif 

// coordinates of pixel by libgdx and spritebatch's color variable
varying vec4 v_color;
varying vec2 v_texCoords;

// texture drawn by libgdx 
uniform sampler2D u_texture;

//my inputs
uniform float cutoff;
uniform float smooth_size;

void main()
{
    vec4 color = v_color * texture2D(u_texture, v_texCoords);
    float value = color.r;
    float alpha = smoothstep(
        cutoff, 
        cutoff + smooth_size, 
        value * (1.0f - smooth_size));
	gl_FragColor = vec4(color.rgb, alpha);
}