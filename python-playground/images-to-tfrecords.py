"""
Create tensorflow TFRecord files from images from the following directory structure:
<input-directory>
    <class1>
        <image1>
        <image2>
        ...
    <class2>
        ...
"""

import getopt
import os
import sys

import tensorflow as tf
import numpy as np
import png
from tqdm import tqdm

try:
    from itertools import imap
except ImportError:
    # For Python 3
    imap=map

def _int64_feature(value):
    return tf.train.Feature(int64_list=tf.train.Int64List(value=[value]))


def _bytes_feature(value):
    return tf.train.Feature(bytes_list=tf.train.BytesList(value=[value]))


def main(argv):
    print("Start converting images into TFRecords.")

    # Parse command line arguments
    usage = """usage: images-to-tfrecords.py -i <images> -o <output-directory>"""

    input_dir_images = None
    output_dir = None

    try:
        opts, args = getopt.getopt(argv, "hi:o:", ["help", "images=", "output="])

    except getopt.GetoptError:
        print(usage)
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print(usage)
            sys.exit()
        elif opt in ("-i", "--images"):
            input_dir_images = arg
        elif opt in ("-o", "--output"):
            output_dir = arg

    if input_dir_images is None or output_dir is None:
        print(usage)
        sys.exit(1)

    if not os.path.exists(input_dir_images):
        raise IOError('Input directory does not exist!')

    # Create output directory if not exists
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    # List class/label folders
    label_names = os.listdir(input_dir_images)

    # Process all images in label folders
    counter = 0
    for i in tqdm(range(len(label_names)), desc="Total"):
        label = label_names[i]
        label_path = os.path.join(input_dir_images, label)

        # List all image files
        image_files = os.listdir(os.path.join(label_path))

        for j in tqdm(range(len(image_files)), desc="     Images with label {}".format(label)):
            image_file = image_files[j]
            if not image_file.endswith(".png"):
                continue
            counter += 1
            # Load image
            image_file_path = os.path.join(label_path, image_file)

            # Create and write TFRecord
            output_file_name = "{}_{}.tfrecord".format(label, os.path.splitext(image_file)[0])
            output_file_path = os.path.join(output_dir, output_file_name)
            writer = tf.python_io.TFRecordWriter(output_file_path)

            reader = png.Reader(image_file_path)
            width, height, pngdata, meta = reader.asDirect()
            bitdepth = meta['bitdepth']
            planes = meta['planes']
            greyscale = meta['greyscale']
            alpha = meta['alpha']

            image_2d = np.vstack(imap(np.uint16, pngdata))
            image = np.reshape(image_2d, (height, width, planes))

            # write label, shape, and image content to the TFRecord file
            example = tf.train.Example(features=tf.train.Features(feature={
                'height':   _int64_feature(height),
                'width':    _int64_feature(width),
                'label':    _int64_feature(int(label)),
                'image':    _bytes_feature(image.tostring())
            }))
            writer.write(example.SerializeToString())
            writer.close()

    print('{} images found.'.format(counter))
    print("Finished.")

if __name__ == "__main__":
    main(sys.argv[1:])
