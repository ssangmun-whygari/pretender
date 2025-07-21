<template>
  <div id="3dModel">
  </div>
</template>

<style scoped>
  .hide {
    visibility: hidden;
  }
</style>

<script setup>
  import * as THREE from 'three'
  import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
  import { ref, onMounted, watch } from 'vue'
  import { useDisplay } from 'vuetify'
  const { lgAndUp } = useDisplay()

  watch(() => { return lgAndUp.value }, (bool) => {
    let _3dmodel = document.getElementById("3dModel")
    if (bool === false) {
      _3dmodel.classList.add("hide")
    } else {
      _3dmodel.classList.remove("hide")
    }
  })

  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera( 30, 500 / 500, 0.1, 1000 ); // 시야각, 종횡비, 절단면(near), 절단면(far)
  camera.position.set(0, 6, 12); // x=0, y=6(위로), z=12(뒤로)
  camera.lookAt(0, 0, 0);
  const renderer = new THREE.WebGLRenderer({ alpha: true });
  renderer.setSize( 300, 300 );

  // 조명
  const light = new THREE.AmbientLight(0xffffff, 1.0);
  scene.add(light);
  // 배경 삭제
  scene.background = null

  // GLB 파일 로드
  const loader = new GLTFLoader();
  let loadedModel;
  loader.load('/models/eumig_film_projector.glb', (gltf) => {
    loadedModel = gltf.scene;
    scene.add(loadedModel);

    // 모델 초기 설정
    loadedModel.scale.set(10, 10, 10); // 크기 조정
    loadedModel.position.y = -2.5

    // 반투명 설정
    loadedModel.traverse((child) => {
      if (child.isMesh) {
        child.material.transparent = true;
        child.material.opacity = 1.0;
      }
    });
  }, undefined, (error) => {
    console.error('GLB 로드 에러:', error);
  });
  
  onMounted(() => {
    const target = document.getElementById('3dModel')
    target.appendChild(renderer.domElement);
    animate();
  });

  function animate() {
    requestAnimationFrame(animate);

    if (loadedModel) {
      loadedModel.rotation.y -= 0.01; // 천천히 회전
    }

    renderer.render(scene, camera);
  }
</script>