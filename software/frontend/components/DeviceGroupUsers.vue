<template>
	<div class="flex flex-col">
		<div class="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
			<div class="py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8">
				<div
					class="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg"
				>
					<table class="min-w-full divide-y divide-gray-200">
						<thead class="bg-gray-50">
							<tr>
								<th
									scope="col"
									class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
								>
									Name
								</th>
								<th
									scope="col"
									class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
								>
									Email
								</th>
								<th
									scope="col"
									class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
								>
									Role
								</th>
								<th
									scope="col"
									class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
								>
									MFA enabled
								</th>
								<th scope="col"></th>
							</tr>
						</thead>
						<tbody>
							<DeviceGroupUserRow
								v-for="(user, userIndex) in users"
								:key="user.user.id"
								:user="user"
								:class="userIndex % 2 === 0 ? 'bg-white' : 'bg-gray-50'"
								@edited="onEdit"
								@deleted="onDelete"
							></DeviceGroupUserRow>
						</tbody>
					</table>
					<!-- Footer -->
					<nav
						class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6"
					>
						<div class="w-full flex">
							<button
								type="button"
								class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
								@click="addModalOpen = true"
							>
								Add User
							</button>
						</div>
					</nav>

					<AddDeviceGroupUserModal
						:open="addModalOpen"
						:device-group-id="props.deviceGroupId"
						@added="onAdd"
						@close="addModalOpen = false"
					></AddDeviceGroupUserModal>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { useApi } from '~/composables/useApi'
	import { useStore } from '~~/stores/main'
	import {
		NotificationType,
		useNotificationStore,
	} from '~~/stores/notification'

	interface Props {
		deviceGroupId: number
	}

	const props = defineProps<Props>()
	const store = useStore()
	const notification = useNotificationStore()

	const addModalOpen = ref(false)

	// TODO: Refresh?

	const {
		data: users,
		pending: loadingUsers,
		refresh: refreshUsers,
	} = await useApi<DeviceGroupUser[]>(
		`/device-groups/${props.deviceGroupId}/users`
	)

	function onAdd(dgu: DeviceGroupUser) {
		users.value.push(dgu)
		notification.show(
			NotificationType.SUCCESS,
			'Successfully added',
			`The User ${dgu.user.username} has been successfully added to this device group`,
			10
		)
	}

	function onEdit(dgu: DeviceGroupUser) {
		const index = users.value.findIndex(
			(user) => user.id.userId === dgu.id.userId
		)
		if (index !== -1) {
			users.value[index] = dgu

			notification.show(
				NotificationType.SUCCESS,
				'User updated',
				`Successfully set the role of ${dgu.user.username} to ${dgu.role}`,
				10
			)
		}
	}

	function onDelete(dgu: DeviceGroupUser) {
		const index = users.value.findIndex(
			(user) => user.id.userId === dgu.id.userId
		)
		if (index !== -1) {
			users.value.splice(index, 1)

			notification.show(
				NotificationType.SUCCESS,
				'User removed',
				`Successfully revoked access from ${dgu.user.username}`,
				10
			)
		}
	}
</script>
