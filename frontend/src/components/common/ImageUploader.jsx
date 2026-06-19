import React, { useState, useRef } from 'react';
import { FaImage, FaTimes, FaSpinner, FaTrash } from 'react-icons/fa';
import { uploadAPI } from '../../api/uploadAPI';
import toast from 'react-hot-toast';

const ImageUploader = ({ onImageUploaded, onRemove, currentImageUrl = null }) => {
  const [imagePreview, setImagePreview] = useState(currentImageUrl);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  const handleFileSelect = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      toast.error('Veuillez sélectionner une image');
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      toast.error('L\'image ne doit pas dépasser 5MB');
      return;
    }

    const previewUrl = URL.createObjectURL(file);
    setImagePreview(previewUrl);

    setUploading(true);
    try {
      const response = await uploadAPI.uploadImage(file);
      const imageUrl = response.data.imageUrl;
      console.log('🔍 [ImageUploader] URL reçue du backend:', imageUrl);
      
      if (onImageUploaded) {
        console.log('🔍 [ImageUploader] Appel de onImageUploaded avec:', imageUrl);
        onImageUploaded(imageUrl);
      }
      
      toast.success('Image uploadée avec succès');
    } catch (error) {
      console.error('🔍 [ImageUploader] Erreur upload:', error);
      toast.error('Erreur lors de l\'upload');
      setImagePreview(currentImageUrl);
    } finally {
      setUploading(false);
    }
  };

  const handleRemove = () => {
    setImagePreview(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
    if (onRemove) {
      onRemove();
    }
  };

  return (
    <div className="mt-3">
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileSelect}
        accept="image/*"
        className="hidden"
      />
      
      {imagePreview ? (
        <div className="relative inline-block">
          <img
            src={imagePreview}
            alt="Aperçu"
            className="h-32 w-auto rounded-lg object-cover border"
          />
          <button
            type="button"
            onClick={handleRemove}
            className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
          >
            <FaTrash size={12} />
          </button>
          {uploading && (
            <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center rounded-lg">
              <FaSpinner className="text-white text-2xl animate-spin" />
            </div>
          )}
        </div>
      ) : (
        <button
          type="button"
          onClick={() => fileInputRef.current.click()}
          disabled={uploading}
          className="flex items-center space-x-2 text-gray-600 hover:text-blue-600"
        >
          {uploading ? (
            <>
              <FaSpinner className="animate-spin" />
              <span>Upload en cours...</span>
            </>
          ) : (
            <>
              <FaImage />
              <span>Ajouter une image</span>
            </>
          )}
        </button>
      )}
    </div>
  );
};

export default ImageUploader;