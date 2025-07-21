<template>
  <h1>TresJS 테스트</h1>

  <v-row>
    <v-col cols="6" style="height: 500px;">
      <TresCanvas render-mode="manual">
        <Suspense>
          <Test_PopCorn/>
        </Suspense>
        <!-- <Suspense>
          <primitive :object="model"/>
        </Suspense> -->
        <TresPerspectiveCamera
          :position="[0, 5, 9]"
          :look-at="[0, 0, 0]"
        />
        <TresDirectionalLight
          :position="[5, 0, 5]"
          :intensity="1"
        ></TresDirectionalLight>
        <TresDirectionalLight
          :position="[-5, 0, 5]"
          :intensity="0.5"
          :color="0xFF8400"
        ></TresDirectionalLight>
        <TresAmbientLight :intensity="1.5" />
      </TresCanvas>
    </v-col>
    <v-col cols="6" style="height: 500px;">
      <TresCanvas>
        <Suspense>
          <Test_MovieCut/>
        </Suspense>
        <TresPerspectiveCamera
          :position="[5, -3, 2]"
          :look-at="[0, 0, 0]"
        />
        <TresSpotLight
          :position="[2, 2, 2]"
          :intensity="15"
        ></TresSpotLight>
      </TresCanvas>
    </v-col>
  </v-row>

</template>

<style scoped>
</style>

<script setup>
import { TresCanvas } from '@tresjs/core'
import Test_PopCorn from './Test_PopCorn.vue';
import Test_MovieCut from './Test_MovieCut.vue';

// import { Mesh, BoxGeometry, MeshBasicMaterial } from 'three'
// // Create a box geometry and a basic material
// const geometry = new BoxGeometry(1, 1, 1)
// const material = new MeshBasicMaterial({ color: 0x00ff00 })
// // Create a mesh with the geometry and material
// const meshWithMaterial = new Mesh(geometry, material)

console.log("inside Test_TresJS.vue...")
// import { ref, shallowRef } from 'vue'
// import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
// import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader.js';
// import { useAnimations } from '@tresjs/cientos'

// const loader = new GLTFLoader();
// const dracoLoader = new DRACOLoader();
// dracoLoader.setDecoderPath('/draco/');  // public 폴더에 복사함
// loader.setDRACOLoader( dracoLoader );


// let model = shallowRef({})
// // Load a glTF resource
// loader.load(
// 	// resource URL
// 	'/models/popcorn_whygari.glb',
// 	// called when the resource is loaded
// 	function ( glb ) {
//     console.log("success!!")
//     console.log(glb)
//     console.log(glb['scene'])
//     console.log(glb['animations'])
//     model.value = glb['scene']
//     startAnimation(model, glb['animations'])
// 	},
// 	// called while loading is progressing
// 	function ( xhr ) {
// 		console.log( ( xhr.loaded / xhr.total * 100 ) + '% loaded' );
// 	},
// 	// called when loading has errors
// 	function ( error ) {
// 		console.log( 'An error happened' );
//     console.error('An error happened', error)
// 	}
// );

// let startAnimation = (model, animations) => {
//   const { actions } = useAnimations(animations, model)
//   const currentAction = ref(actions.cupAction)
//   currentAction.value.play()
// }
</script>