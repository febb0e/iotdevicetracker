<template>
	<div id="app" class="bg-gray-100 h-screen">
		<Sidebar></Sidebar>
		<div class="md:pl-64 h-full flex flex-col">
			<Header></Header>
			<main class="container flex-grow mx-auto max-w-7xl px-4 sm:px-6 md:px-8f">
				<NuxtPage></NuxtPage>
			</main>
		</div>
		<Notifications></Notifications>
	</div>
</template>

<script lang="ts" setup>
	import '~/assets/css/main.css'
</script>
