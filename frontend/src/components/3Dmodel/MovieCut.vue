<template>
  <primitive :object="model" />
</template>

<script setup>
// reference : https://docs.tresjs.org/cookbook/load-models.html
import { ref } from 'vue'
import { useAnimations, useGLTF } from '@tresjs/cientos'
const { scene: model, animations } = await useGLTF('/models/movie_cut_whygari.glb', { draco: true })
const { actions } = useAnimations(animations, model)
const currentAction = ref(actions.topBarAction)
currentAction.value.play()
</script>