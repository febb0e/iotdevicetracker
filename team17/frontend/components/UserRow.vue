<template>
	<tr>
		<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
			{{ user.username }}
		</td>
		<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
			{{ user.email }}
		</td>
		<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
			{{ user.verified ? 'Yes' : 'No' }}
		</td>
		<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
			{{ user.mfaEnabled ? 'Enabled' : 'Disabled' }}
		</td>
		<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
			{{ user.role }}
		</td>
		<td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
			<button
				type="button"
				class="inline-flex items-center p-1 border border-transparent rounded-full shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
				@click="editModalOpen = true"
			>
				<PencilIcon class="h-5 w-5" aria-hidden="true" />
			</button>
			<button
				v-if="props.user.id !== store.authenticatedUser.id"
				type="button"
				class="inline-flex items-center p-1 ml-2 border border-transparent rounded-full shadow-sm text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
				@click="deleteModalOpen = true"
			>
				<TrashIcon class="h-5 w-5" aria-hidden="true" />
			</button>
		</td>

		<EditUserModal
			:open="editModalOpen"
			:user="user"
			@edited="onEdit"
			@close="editModalOpen = false"
		></EditUserModal>

		<DeleteUserModal
			:open="deleteModalOpen"
			:user="user"
			@deleted="onDelete"
			@close="deleteModalOpen = false"
		></DeleteUserModal>
	</tr>
</template>

<script lang="ts" setup>
	import { useStore } from '~~/stores/main'
	import { PencilIcon, TrashIcon } from '@heroicons/vue/solid/index.js'

	interface Props {
		user: User
	}

	interface Emits {
		(e: 'edited', user: User): void
		(e: 'deleted', user: User): void
	}

	const props = defineProps<Props>()
	const emit = defineEmits<Emits>()
	const store = useStore()
	const editModalOpen = ref(false)
	const deleteModalOpen = ref(false)

	function onEdit(user: User) {
		emit('edited', user)
	}

	function onDelete() {
		emit('deleted', props.user)
	}
</script>
