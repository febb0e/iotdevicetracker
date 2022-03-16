<template>
	<div>
		<TransitionRoot as="template" :show="store.sidebarOpen">
			<Dialog
				as="div"
				class="fixed inset-0 flex z-40 md:hidden"
				@close="store.sidebarOpen = false"
			>
				<TransitionChild
					as="template"
					enter="transition-opacity ease-linear duration-300"
					enter-from="opacity-0"
					enter-to="opacity-100"
					leave="transition-opacity ease-linear duration-300"
					leave-from="opacity-100"
					leave-to="opacity-0"
				>
					<DialogOverlay class="fixed inset-0 bg-gray-600 bg-opacity-75" />
				</TransitionChild>
				<TransitionChild
					as="template"
					enter="transition ease-in-out duration-300 transform"
					enter-from="-translate-x-full"
					enter-to="translate-x-0"
					leave="transition ease-in-out duration-300 transform"
					leave-from="translate-x-0"
					leave-to="-translate-x-full"
				>
					<div
						class="relative flex-1 flex flex-col max-w-xs w-full pt-5 pb-4 bg-indigo-700"
					>
						<TransitionChild
							as="template"
							enter="ease-in-out duration-300"
							enter-from="opacity-0"
							enter-to="opacity-100"
							leave="ease-in-out duration-300"
							leave-from="opacity-100"
							leave-to="opacity-0"
						>
							<div class="absolute top-0 right-0 -mr-12 pt-2">
								<button
									type="button"
									class="ml-1 flex items-center justify-center h-10 w-10 rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white"
									@click="store.sidebarOpen = false"
								>
									<span class="sr-only">Close sidebar</span>
									<XIcon class="h-6 w-6 text-white" aria-hidden="true" />
								</button>
							</div>
						</TransitionChild>
						<div class="flex-1 h-0 overflow-y-auto">
							<nav class="px-2 space-y-1">
								<nuxt-link
									v-for="item in navigation"
									:key="item.name"
									:to="item.to"
									:class="[
										item.current
											? 'bg-indigo-800 text-white'
											: 'text-indigo-100 hover:bg-indigo-600',
										'group flex items-center px-2 py-2 text-base font-medium rounded-md',
									]"
								>
									<component
										:is="item.icon"
										class="mr-4 flex-shrink-0 h-6 w-6 text-indigo-300"
										aria-hidden="true"
									/>
									{{ item.name }}
								</nuxt-link>
							</nav>
						</div>
					</div>
				</TransitionChild>
				<div class="flex-shrink-0 w-14" aria-hidden="true">
					<!-- Dummy element to force sidebar to shrink to fit close icon -->
				</div>
			</Dialog>
		</TransitionRoot>

		<!-- Static sidebar for desktop -->
		<div class="hidden md:flex md:w-64 md:flex-col md:fixed md:inset-y-0">
			<!-- Sidebar component, swap this element with another sidebar if you like -->
			<div class="flex flex-col flex-grow pt-5 bg-indigo-700 overflow-y-auto">
				<div class="flex-1 flex flex-col">
					<nav class="flex-1 px-2 pb-4 space-y-1">
						<nuxt-link
							v-for="item in navigation"
							v-show="item.show.value"
							:key="item.name"
							:to="item.to"
							:class="[
								$route.path === item.to
									? 'bg-indigo-800 text-white'
									: 'text-indigo-100 hover:bg-indigo-600',
								'group flex items-center px-2 py-2 text-sm font-medium rounded-md',
							]"
						>
							<component
								:is="item.icon"
								class="mr-3 flex-shrink-0 h-6 w-6 text-indigo-300"
								aria-hidden="true"
							/>
							{{ item.name }}
						</nuxt-link>
					</nav>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import {
		Dialog,
		DialogOverlay,
		TransitionChild,
		TransitionRoot,
	} from '@headlessui/vue'
	import {
		FolderIcon,
		HomeIcon,
		UsersIcon,
		XIcon,
	} from '@heroicons/vue/outline/index.js'
	import { useStore } from '~~/stores/main'

	const store = useStore()

	const isAdmin = computed(
		() => store.authenticatedUser?.role === 'SYSADMIN' ?? false
	)

	const navigation = [
		{ name: 'Dashboard', to: '/', icon: HomeIcon, show: computed(() => true) },
		{ name: 'Users', to: '/admin/users', icon: UsersIcon, show: isAdmin },
		{
			name: 'Device Groups',
			to: '/admin/device-groups',
			icon: FolderIcon,
			show: isAdmin,
		},
	]
</script>
