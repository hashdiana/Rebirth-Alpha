#ifdef GL_ES
precision highp float;
#endif
 
 
uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
uniform sampler2D texture;

#define iterations 0
#define formuparam2 0.79
 
#define volsteps 9
#define stepsize 0.190
 
#define zoom 1.900
#define tile  .50

#define speed2  0.0
#define cloudSpeed 0.0
#define twinkle 0.00

 
#define brightness 0.008
#define darkmatter 8.700
#define distfading 0.760
#define saturation 0.850


#define transverseSpeed 0.0 //zoom*2.0
#define cloud 0.09 

 
float triangle(float x, float a) { 
    float output2 = 2.0*abs(  2.0*  ( (x/a) - floor( (x/a) + 0.5) ) ) - 1.0;
    return output2;
}
 
float field(in vec3 p) {
    float cloudTime = time*cloudSpeed;
    float strength = 7. + .03 * log(1.e-6 + fract(sin(cloudTime) * 4373.11));
    float accum = 0.;
    float prev = 0.;
    float tw = 0.;  

    for (int i = 0; i < 9; ++i) {
        float mag = dot(p, p);
        p = abs(p) / mag + vec3(-.5, -.8 + 0.1*sin(cloudTime*0.7 + 2.0), -1.1+0.3*cos(cloudTime*0.3));
        float w = exp(-float(i) / 7.);
        accum += w * exp(-strength * pow(abs(mag - prev), 2.3));
        tw += w;
        prev = mag;
    }
    return max(0., 5. * accum / tw - .7);
}

float glowShader() {
    float radius = 2.5;
    float quality = 1.0;
    float divider = 158.0;
    float maxSample = 10.0;
    vec2 texelSize = vec2(1.0 / resolution.x * (radius * quality), 1.0 / resolution.y * (radius * quality));
    float alpha = 0;

    for (float x = -radius; x < radius; x++) {
        for (float y = -radius; y < radius; y++) {
            vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y));

            if (currentColor.a != 0)
            alpha += divider > 0 ? max(0.0, (maxSample - distance(vec2(x, y), vec2(0))) / divider) : 1;
        }
    }

    return alpha;
}

float calcLuma(vec3 color) {
    return 0.299*color.r + 0.587*color.g + 0.114*color.b;
}

void main() {   
        vec2 uv2 = gl_FragCoord.xy/resolution.xy-0.5;
    uv2.y *= resolution.y/resolution.x;
    vec2 uvs = uv2;
    
    float time2 = time;               
        
        float speed = - speed2 * cos(time2*0.02 + 3.1415926/4.0);          
    //speed = 0.0;  
        float formuparam = formuparam2;
    
        //get coords and direction  
    vec2 uv = uvs;             
    //mouse rotation
    float a_xz = 0.9;
    float a_yz = -2.6;
    float a_xy = 0.9;// + time*0.04;    
    
    mat2 rot_xz = mat2(cos(a_xz),sin(a_xz),-sin(a_xz),cos(a_xz));   
    mat2 rot_yz = mat2(cos(a_yz),sin(a_yz),-sin(a_yz),cos(a_yz));       
    mat2 rot_xy = mat2(cos(a_xy),sin(a_xy),-sin(a_xy),cos(a_xy));
    

    float v2 =1.0;  
    vec3 dir=vec3(uv*zoom,1.); 
    vec3 from=vec3(0.0, 0.0,0.0);                               
        
    from.x -= 0.1*(mouse.x-0.5);
    from.y -= 0.1*(mouse.y-0.5);
    
    
    from.x += 10.0*cos(0.004*time);
    from.y += 10.0*sin(0.00777777*time);
    from.z += 0.003*time;
           
       
    vec3 forward = vec3(0.,0.,1.0 - twinkle);   
    
    dir.xy*=rot_xy;
    dir.xz*=rot_xz;
    dir.yz*= rot_yz;
    
    forward.xy *= rot_xy;
    forward.xz *= rot_xz;
    forward.yz *= rot_yz;
    
    from.xy*=rot_xy;
    from.xz*=rot_xz;
    from.yz*= rot_yz;
    
    
    //zoom
    float zooom = (time2)*speed;
    
    from += forward* zooom*1.0;
    
    float sampleShift = mod( zooom, stepsize );
     
    float zoffset = -sampleShift;
    sampleShift /= stepsize; // make from 0 to 1
    
    //volumetric rendering
    float s=0.74;
    float s3 = s + stepsize/2.0;
    vec3 v=vec3(0.);
    float t3 = 0.0; 
    
    vec3 backCol2 = vec3(0.);
    for (int r=0; r<volsteps; r++) {
        vec3 p2=from+(s+zoffset)*dir;// + vec3(0.,0.,zoffset);
        vec3 p3=from+(s3+zoffset)*dir;// + vec3(0.,0.,zoffset);
        
        p2 = abs(vec3(tile)-mod(p2,vec3(tile*2.))); // tiling fold
        p3 = abs(vec3(tile)-mod(p3,vec3(tile*2.))); // tiling fold      
        #ifdef cloud
        t3 = field(p3);
        #endif
        
        float pa,a=pa=0.;
        for (int i=0; i<iterations; i++) {
            p2=abs(p2)/dot(p2,p2)-formuparam; // the magic formula
            //p=abs(p)/max(dot(p,p),0.005)-formuparam; // another interesting way to reduce noise
            float D = abs(length(p2)-pa); // absolute sum of average change
            a += i > 7 ? min( 12., D) : D;
            pa=length(p2);
        }
        
        
        //float dm=max(0.,darkmatter-a*a*.001); //dark matter
        a*=a*a; // add contrast
        //if (r>3) fade*=1.-dm; // dark matter, don't render near
        // brightens stuff up a bit
        float s1 = s+zoffset;
        // need closed form expression for this, now that we shift samples
        float fade = pow(distfading,max(0.,float(r)-sampleShift));      
        //t3 += fade;       
        v+=fade;
            //backCol2 -= fade;

        // fade out samples as they approach the camera
        if( r == 0 )
            fade *= (1. - (sampleShift));
        // fade in samples as they approach from the distance
        if( r == volsteps-1 )
            fade *= sampleShift;
        v+=vec3(s1,s1*s1,s1*s1*s1*s1)*a*brightness*fade; // coloring based on distance
        
        backCol2 += mix(.4, 1., v2) * vec3(1.8 * t3 * t3 * t3, 1.4 * t3 * t3, t3) * fade;

        
        s+=stepsize;
        s3 += stepsize;     
    }//����
               
    v=mix(vec3(length(v)),v,saturation); //color adjust 

    vec3 forCol2 = vec3(v*.01);
    #ifdef cloud
    backCol2 *= cloud;
    #endif  
    backCol2.b *= 1.8;
    backCol2.r *= 0.05; 
    
    backCol2.b = 0.5*mix(backCol2.g, backCol2.b, 0.8);
    backCol2.g = 0.0;
//  backCol2.bg = mix(backCol2.gb, backCol2.bg, 0.5*(cos(time*0.01) + 1.0));

    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);
    float alpha = 0;

    vec3 result = forCol2 + backCol2;

    if (centerCol.a != 0) {
        alpha = 1.0;
    } else {
        alpha = glowShader();
    }

    gl_FragColor = vec4(result, alpha);
}