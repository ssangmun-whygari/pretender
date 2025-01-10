<template>
  <h1>3d 모델 테스트</h1>
  <div id="target" style="border: 1px solid;"></div>
</template>

<style scoped>
</style>

<script setup>
  // reference : https://stackoverflow.com/questions/76987908/how-to-implement-the-three-js-scene-example-in-vue-3
  import * as THREE from 'three'
  import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
  import { ref, onMounted } from 'vue'
  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera( 30, 500 / 500, 0.1, 1000 ); // 시야각, 종횡비, 절단면(near), 절단면(far)
  camera.position.set(0, 6, 12); // x=0, y=5(위로), z=10(뒤로)
  camera.lookAt(0, 0, 0);
  const renderer = new THREE.WebGLRenderer({ alpha: true });
  renderer.setSize( 500, 500 );

  // const geometry = new THREE.BoxGeometry( 1, 1, 1 );
  // const material = new THREE.MeshBasicMaterial( { color: 0x00ff00 } );
  // const cube = new THREE.Mesh( geometry, material );
  // scene.add( cube );

  // 조명
  const light = new THREE.AmbientLight(0xffffff, 1.0);
  scene.add(light);
  // 배경 삭제
  scene.background = null

  // const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
  // directionalLight.position.set(5, 5, 5);
  // scene.add(directionalLight);

  // GLB 로드
  const loader = new GLTFLoader();
  let loadedModel;
  loader.load('../public/models/eumig_film_projector.glb', (gltf) => {
    loadedModel = gltf.scene;
    scene.add(loadedModel);

    // 모델 초기 설정
    loadedModel.scale.set(10, 10, 10); // 크기 조정
    loadedModel.position.y = -2

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

  function animate() {
    requestAnimationFrame(animate);

    // cube.rotation.x += 0.01;
    // cube.rotation.y += 0.01;

    if (loadedModel) {
      loadedModel.rotation.y -= 0.01; // 천천히 회전
    }

    renderer.render(scene, camera);
  }

onMounted(() => {
  console.log("=======================onMounted...")
  const target = document.getElementById('target')
  console.log(target)
  // console.log(target.value)
  console.log(renderer.domElement)
  target.appendChild(renderer.domElement);
  animate();
});
</script>