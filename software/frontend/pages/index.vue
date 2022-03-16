<template>
	<div class="mt-6">
		<div
			class="flex justify-between items-center mb-4 pb-5 border-b border-gray-200"
		>
			<h3 class="text-lg leading-6 font-medium text-gray-900">
				My Device Groups
			</h3>
			<button
				type="button"
				class="px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3"
				@click="refresh()"
			>
				<span v-if="pending">Reloading ...</span>
				<span v-else>Reload</span>
			</button>
		</div>

		<CreateDeviceGroupModal
			:open="createModalOpen"
			@created="onCreate"
			@close="createModalOpen = false"
		></CreateDeviceGroupModal>

		<div v-if="error" class="mt-6 rounded-md bg-red-100 p-4">
			<div class="flex">
				<div class="flex-shrink-0">
					<XCircleIcon
						class="h-5 w-5 text-red-400"
						aria-hidden="true"
					></XCircleIcon>
				</div>
				<div class="ml-3">
					<h3 class="text-sm font-medium text-red-800">
						Internal server error
					</h3>
					<div class="mt-2 text-sm text-red-700">
						<p>
							There was an error while loading the device groups. Please try
							again. If the issue persists, please contact an administrator.
						</p>
					</div>
				</div>
			</div>
		</div>

		<div v-else class="grid grid-cols-1 gap-4 sm:grid-cols-2">
			<nuxt-link
				v-for="group in data.deviceGroups"
				:key="group.id"
				:to="`/device-groups/${group.id}`"
				class="relative rounded-lg border border-gray-300 bg-white px-6 py-5 shadow-sm flex items-center space-x-3 hover:border-gray-400 focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-indigo-500"
			>
				<div class="flex-1 min-w-0">
					<p class="text-sm font-medium text-gray-900">
						{{ group.name }}
						<span
							class="inline-block ml-2 px-2 py-0.5 text-green-800 text-xs font-medium bg-green-100 rounded-full"
							>{{ group.role }}</span
						>
					</p>
					<p class="text-sm text-gray-500 truncate">
						<template v-if="group.amountOfDevices === 1">1 Device</template>
						<template v-else>{{ group.amountOfDevices }} Devices</template>
					</p>
				</div>
			</nuxt-link>
			<div
				class="relative flex items-center justify-center rounded-lg border border-gray-300 border-dashed bg-white px-6 py-5 hover:border-gray-400 focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-indigo-500 text-gray-700 cursor-pointer"
				@click="createModalOpen = true"
			>
				<div class="flex-1 min-w-0">
					<p class="text-sm font-medium text-gray-900">
						Create Device Group ...
					</p>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { XCircleIcon } from '@heroicons/vue/solid/index.js'
	import { useApi } from '~/composables/useApi'
	import { NotificationType, useNotificationStore } from '~/stores/notification'

	const { data, pending, refresh, error } = await useApi<{
		deviceGroups: DeviceGroup[]
	}>('/device-groups')

	const notification = useNotificationStore()
	const createModalOpen = ref(false)

	function onCreate(deviceGroup: DeviceGroup) {
		data.value.deviceGroups.push(deviceGroup)
		notification.show(
			NotificationType.SUCCESS,
			'Successfully created',
			`The Device Group ${deviceGroup.name} has been successfully created`,
			10
		)
	}
</script>
