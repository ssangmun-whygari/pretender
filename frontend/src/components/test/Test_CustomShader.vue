<template>
  <h1>셰이더</h1>
  <div ref="container" style="width: 100vw; height: 100vh;"></div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import * as THREE from 'three'

const container = ref(null)

onMounted(() => {
  const scene = new THREE.Scene()
  const camera = new THREE.Camera()

  const renderer = new THREE.WebGLRenderer()
  renderer.setSize(window.innerWidth, window.innerHeight)
  container.value.appendChild(renderer.domElement)

  const resolution = new THREE.Vector2(window.innerWidth, window.innerHeight)

  const uniforms = {
    iTime: { value: 0.0 },
    iResolution: { value: resolution }
  }

  const material = new THREE.ShaderMaterial({
    uniforms,
    fragmentShader: `
uniform float iTime;
uniform vec2 iResolution;

void main() {
    vec2 uv = gl_FragCoord.xy / iResolution.xy;
    uv.x *= iResolution.x / iResolution.y;

    float gridSize = 10.0;

    // // 왼쪽으로 이동
    // uv.x += iTime * 0.2;

    // 대각선 위로 이동
    float speed = 0.1;
    uv.x += iTime * speed;
    uv.y -= iTime * speed;

    vec2 gridUV = floor(uv * gridSize);
    vec2 localUV = fract(uv * gridSize);

    // Checkerboard 구분
    float checker = mod(gridUV.x + gridUV.y, 2.0);

    // checker가 0이면 위상 0, 1이면 π (반대 타이밍)
    float phase = checker * 3.14159;

    // 같은 주기, 위상만 반대
    float scale = 0.25 * cos(iTime * 2.0 + phase) + 0.25;

    vec2 dist = localUV - 0.5;         // 중심으로부터 거리
    float radius = length(dist);       // 원형 거리
    float circle = step(radius, scale);  // 원 마스크

    // vec3 color = vec3(0.0, 0.631, 1.0); // 하늘색 color
    vec3 color = vec3(0.505, 0.82, 1.0); // 하늘색 color
    vec3 bg = vec3(1.0);  // 흰 배경
    vec3 renderedColor = mix(bg, color, circle);

    gl_FragColor = vec4(renderedColor, 1.0);
}
    `,
    vertexShader: `
      void main() {
        gl_Position = vec4(position, 1.0);
      }
    `
  })

  const geometry = new THREE.PlaneGeometry(2, 2)
  const mesh = new THREE.Mesh(geometry, material)
  scene.add(mesh)

  function animate(time) {
    uniforms.iTime.value = time * 0.001
    renderer.render(scene, camera)
    requestAnimationFrame(animate)
  }
  animate()
})
</script>