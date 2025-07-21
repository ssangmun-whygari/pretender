<template>
  <primitive :object="model" />
</template>

<script setup>
// reference : https://docs.tresjs.org/cookbook/load-models.html
import { ref } from 'vue'
import { useAnimations, useGLTF } from '@tresjs/cientos'
import { useTresContext, useLoop } from '@tresjs/core'
console.log("moviecut start...")
const { nodes, animations } = await useGLTF('/models/movie_cut_whygari.glb', { draco: true })
console.log("moviecut 비동기 함수 end...")
console.log(nodes)
const model = nodes['Scene']
const { actions } = useAnimations(animations, model)
const currentAction = ref(actions.topBarAction)

// const { renderer, scene, camera } = useTresContext();
// const { render } = useLoop()
// console.log("context in moviecut...");
// render(({ renderer, scene, camera }) => {
//   renderer.render(scene, camera)
// })

currentAction.value.play()
</script>