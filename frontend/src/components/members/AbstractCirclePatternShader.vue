<template>
  <div ref="container" style="width: 100%; height: 100%;"></div>
</template>

<script setup>
  import { onBeforeUnmount, onMounted, ref, nextTick } from 'vue'
  import * as THREE from 'three'

  const container = ref(null) // div 객ㄱ체
  let renderer, scene, camera, uniforms
  const size = ref({ width: 0, height: 0 })
  let resizeObserver

  onMounted(() => {
    scene = new THREE.Scene()
    camera = new THREE.Camera()

    renderer = new THREE.WebGLRenderer()
    container.value.appendChild(renderer.domElement)

    const width = container.value.clientWidth
    const height = container.value.clientHeight
    renderer.setSize(width, height)

    uniforms = {
      iTime: { value: 0.0 },
      iResolution: { value: new THREE.Vector2() }
    }
    uniforms.iResolution.value.set(width, height)

    const material = new THREE.ShaderMaterial({
      uniforms,
      fragmentShader: `
      uniform float iTime;
      uniform vec2 iResolution;

      void main() {
          vec2 uv = gl_FragCoord.xy / iResolution.xy;
          uv.x *= iResolution.x / iResolution.y;

          float gridSize = 9.0;

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

    nextTick(() => {

      resizeObserver = new ResizeObserver(entries => {
      for (const entry of entries) {
        const { width, height } = entry.contentRect
        renderer.setSize(width, height)
        uniforms.iResolution.value.set(width, height)
        }
      })
      resizeObserver.observe(container.value)


    })


  })

  onBeforeUnmount(() => {
    resizeObserver.disconnect()
  })
</script>