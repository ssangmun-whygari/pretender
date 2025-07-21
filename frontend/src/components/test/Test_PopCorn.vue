<template>
  <primitive :object="model" />
</template>

<script setup>
import { shallowRef } from 'vue'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader.js';
import { AnimationMixer } from 'three'
import { useLoop } from '@tresjs/core'

const model = shallowRef({})
const mixer = shallowRef(null)
const loader = new GLTFLoader();
const dracoLoader = new DRACOLoader();
dracoLoader.setDecoderPath('/draco/');  // public 폴더에 복사함
loader.setDRACOLoader( dracoLoader );

// Load a glTF resource
loader.load(
	// resource URL
	'/models/popcorn_whygari.glb',
	// called when the resource is loaded
	function ( glb ) {
    console.log("success!!!!!!!")
    console.log(glb)
    console.log(glb['scene'])
    console.log(glb['animations'])

    model.value = glb.scene
    startAnimation(model.value, glb['animations'])
	},
	// called while loading is progressing
	function ( xhr ) {
		console.log( ( xhr.loaded / xhr.total * 100 ) + '% loaded' );
	},
	// called when loading has errors
	function ( error ) {
		console.log( 'An error happened' );
    console.error('An error happened', error)
	}
);

let startAnimation = (model, animations) => {
  const clip = animations[0]
  console.log("clip : ....")
  console.log(clip)
  console.log("clip : .... end")
  mixer.value = new AnimationMixer(model)
  const action = mixer.value.clipAction(clip)

  console.log("start animation...")
  console.log(action)

  action.play()
}

const { render, onBeforeRender, pause, resume } = useLoop()

render(({ renderer, scene, camera }) => {
  renderer.render(scene, camera)
})

onBeforeRender(({ delta, clock }) => {
  if (mixer.value) {
    mixer.value.update(delta) 
  }
})
</script>