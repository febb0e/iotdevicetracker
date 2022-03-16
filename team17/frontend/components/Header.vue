<template>
	<div class="sticky top-0 z-10 flex-shrink-0 flex h-16 bg-white shadow">
		<button
			type="button"
			class="px-4 border-r border-gray-200 text-gray-500 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-indigo-500 md:hidden"
			@click="store.sidebarOpen = true"
		>
			<span class="sr-only">Open sidebar</span>
			<MenuAlt2Icon class="h-6 w-6" aria-hidden="true" />
		</button>
		<div class="flex w-full items-center justify-end mr-4 md:mr-6">
			<!-- Profile dropdown -->
			<Menu v-if="store.authenticatedUser" as="div" class="relative">
				<div>
					<MenuButton
						class="max-w-xs bg-white flex items-center text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
					>
						<ChevronDownIcon class="mr-2 h-5 w-5"></ChevronDownIcon>
						<span>{{ store.authenticatedUser.username }}</span>
					</MenuButton>
				</div>
				<transition
					enter-active-class="transition ease-out duration-100"
					enter-from-class="transform opacity-0 scale-95"
					enter-to-class="transform opacity-100 scale-100"
					leave-active-class="transition ease-in duration-75"
					leave-from-class="transform opacity-100 scale-100"
					leave-to-class="transform opacity-0 scale-95"
				>
					<MenuItems
						class="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
					>
						<MenuItem
							v-for="item in userNavigation"
							:key="item.name"
							v-slot="{ active }"
						>
							<nuxt-link
								:to="item.to"
								:class="[
									active ? 'bg-gray-100' : '',
									'block px-4 py-2 text-sm text-gray-700',
								]"
								>{{ item.name }}</nuxt-link
							>
						</MenuItem>
						<MenuItem v-slot="{ active }" @click="logout">
							<span
								:class="[
									active ? 'bg-gray-100' : '',
									'cursor-pointer block px-4 py-2 text-sm text-gray-700',
								]"
								>Logout</span
							>
						</MenuItem>
					</MenuItems>
				</transition>
			</Menu>

			<!-- Login button-->
			<div v-else>
				<NuxtLink to="/auth/login">
					<button
						type="button"
						class="order-0 px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3"
					>
						Login
					</button>
				</NuxtLink>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { Menu, MenuButton, MenuItem, MenuItems } from '@headlessui/vue'
	import { MenuAlt2Icon } from '@heroicons/vue/outline/index.js'
	import { ChevronDownIcon } from '@heroicons/vue/solid/index.js'
	import { useStore } from '~/stores/main'
	import { NotificationType, useNotificationStore } from '~/stores/notification'
	import { $api } from '~/composables/useApi'

	const store = useStore()
	const router = useRouter()
	const notification = useNotificationStore()
	const userNavigation = [{ name: 'Settings', to: '/user-settings' }]

	async function logout() {
		try {
			await $api('/auth/logout', { method: 'POST' })
			notification.show(
				NotificationType.INFO,
				`See you next time, ${store.authenticatedUser.username}!`,
				'Successfully logged out',
				10
			)
			store.authenticatedUser = null
			router.push('/auth/login')
		} catch (err) {
			notification.internalServerError()
			console.error(err)
		}
	}
</script>
