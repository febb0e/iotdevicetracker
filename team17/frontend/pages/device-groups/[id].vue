<template>
	<div class="mt-6">
		<div class="flex items-center mb-4 pb-5 border-b border-gray-200">
			<h3 class="text-lg leading-6 font-medium text-gray-900">
				{{ deviceGroup.name }}
				<span
					class="flex-shrink-0 inline-block px-2 py-0.5 text-green-800 text-sm font-medium bg-green-100 rounded-full"
					>{{ deviceGroup.role }}</span
				>
			</h3>
			<div class="flex-grow"></div>
			<button
				v-show="deviceGroup.role === 'ADMIN'"
				type="button"
				class="order-0 px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3"
				@click="editModalOpen = true"
			>
				Edit
			</button>
			<button
				v-show="deviceGroup.role === 'ADMIN'"
				type="button"
				class="order-0 px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:ml-3"
				@click="deleteModalOpen = true"
			>
				Delete
			</button>
		</div>

		<div class="mb-2">
			<h3 class="text-md leading-6 font-medium text-gray-900">Devices</h3>
		</div>

		<div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
			<nuxt-link
				v-for="device in data.devices"
				:key="device.id"
				:to="`/devices/${device.id}`"
				class="relative rounded-lg border border-gray-300 bg-white px-6 py-5 shadow-sm flex items-center space-x-3 hover:border-gray-400 focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-indigo-500"
			>
				<div class="flex-1 min-w-0">
					<span class="absolute inset-0" aria-hidden="true" />
					<p class="text-sm font-medium text-gray-900">
						{{ device.name }}
					</p>
					<p class="text-sm text-gray-500 truncate">
						{{ device.identifier }}
					</p>
				</div>
			</nuxt-link>
			<div
				v-if="deviceGroup.role === 'ADMIN'"
				class="relative flex items-center justify-center rounded-lg border border-gray-300 border-dashed bg-white px-6 py-5 hover:border-gray-400 focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-indigo-500 text-gray-700 cursor-pointer"
				@click="createDeviceModalOpen = true"
			>
				Create Device ...
			</div>
		</div>

		<div class="mt-8 mb-2">
			<h3 class="text-md leading-6 font-medium text-gray-900">Users</h3>
		</div>

		<DeviceGroupUsers
			v-if="deviceGroup.role === 'ADMIN'"
			:device-group-id="deviceGroupId"
		></DeviceGroupUsers>
		<div v-else class="mt-6 rounded-md bg-yellow-50 p-4">
			<div class="flex">
				<div class="flex-shrink-0">
					<ExclamationIcon
						class="h-5 w-5 text-yellow-400"
						aria-hidden="true"
					></ExclamationIcon>
				</div>
				<div class="ml-3">
					<h3 class="text-sm font-medium text-yellow-800">
						Missing permissions
					</h3>
				</div>
			</div>
		</div>

		<EditDeviceGroupModal
			:open="editModalOpen"
			:device-group="deviceGroup"
			@edited="onEdit"
			@close="editModalOpen = false"
		></EditDeviceGroupModal>

		<DeleteDeviceGroupModal
			:open="deleteModalOpen"
			:device-group="deviceGroup"
			@deleted="onDelete"
			@close="deleteModalOpen = false"
		></DeleteDeviceGroupModal>

		<CreateDeviceModal
			:open="createDeviceModalOpen"
			:device-group-id="deviceGroup.id"
			@created="onCreate"
			@close="createDeviceModalOpen = false"
		></CreateDeviceModal>
	</div>
</template>

<script lang="ts" setup>
	import { useStore } from '~~/stores/main'
	import {
		NotificationType,
		useNotificationStore,
	} from '~~/stores/notification'
	import { ExclamationIcon } from '@heroicons/vue/solid/index.js'

	const store = useStore()
	const router = useRouter()
	const notification = useNotificationStore()
	const route = useRoute()
	const deviceGroupId = parseInt(route.params.id as string, 10)

	const editModalOpen = ref(false)
	const deleteModalOpen = ref(false)
	const createDeviceModalOpen = ref(false)

	const [
		{ data: deviceGroup },
		{ data, pending: loadingDevices, refresh: refreshDevices },
	] = await Promise.all([
		useApi<DeviceGroup>(`/device-groups/${deviceGroupId}`, {
			pick: ['name', 'role'],
		}),
		useApi<{ devices: Device[] }>('/devices', { params: { deviceGroupId } }),
	])

	function onEdit(dg: DeviceGroup) {
		notification.show(
			NotificationType.SUCCESS,
			'Successfully updated',
			`The Device Group ${deviceGroup.value.name} has been successfully updated`,
			10
		)
		deviceGroup.value = dg
	}

	function onDelete() {
		notification.show(
			NotificationType.SUCCESS,
			'Successfully deleted',
			`The Device Group ${deviceGroup.value.name} has been successfully deleted`,
			10
		)
		router.push('/')
	}

	function onCreate(device: DeviceWithGroup) {
		data.value.devices.push(device)
	}
</script>
