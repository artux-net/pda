#ifdef GL_ES

#define LOWP lowp
precision mediump float;
#else

#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float red_value;

void main() {
	gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
	if(gl_FragColor.r < red_value){
		gl_FragColor.r = red_value;
	}
}