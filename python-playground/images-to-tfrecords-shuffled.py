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
    
    input_files = []
    for folder in os.listdir(input_dir_images):
        input_files_sub = os.listdir(os.path.join(input_dir_images, folder))
        input_files_sub = list(map(lambda f: os.path.join(os.path.join(input_dir_images, folder), f), input_files_sub))
        input_files_sub = list(filter(lambda f: f.lower().endswith('.png'), input_files_sub))
        input_files_sub = list(map(lambda f: (f, folder), input_files_sub))
        input_files.extend(input_files_sub)
        
    print("{} files founds.".format(len(input_files)))
    np.random.shuffle(input_files)
    
    # Process all images in label folders
    output_file_path = os.path.join(output_dir, 'data.tfrecords')
    writer = tf.python_io.TFRecordWriter(output_file_path)
    
    for i in tqdm(range(len(input_files))):
        image_file = input_files[i][0]
        label = input_files[i][1]
        
        reader = png.Reader(image_file)
        width, height, pngdata, meta = reader.asDirect()
        bitdepth = meta['bitdepth']
        planes = meta['planes']
        greyscale = meta['greyscale']
        alpha = meta['alpha']

        image_2d = np.vstack(imap(np.uint8, pngdata))
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
    print("Finished.")

if __name__ == "__main__":
    main(sys.argv[1:])
