import request from '@/utils/request.api'

export type UploadFileResponse = {
  url: string
  path: string
  originalName: string
}

export const uploadApi = {
  uploadImage: async (file: File, folder = 'images') => {
    const form = new FormData()
    form.append('file', file)
    form.append('folder', folder)
    return (await request.post('/files/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })) as unknown as UploadFileResponse
  }
}

