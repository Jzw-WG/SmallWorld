import cv2
import numpy as np
import math
import random


class small_world:

    def __init__(self, world_size, connect_number, reconnect_rate):
        assert connect_number % 2 == 0
        self.img_size = [640, 640, 3]
        self.img_r = 300
        self.world_size = world_size
        self.connect_number = int(connect_number / 2)
        self.reconnect_rate = reconnect_rate
        self.connect_mat = self.get_connect_mat()

    def get_img(self):
        img = np.zeros(self.img_size, dtype=np.uint8)
        for i in range(self.world_size):
            point_x_y = self.get_point_x_y(i)
            img[point_x_y[0:2]] = (255, 0, 0)

        for i in range(self.connect_mat.shape[0]):
            for j in range(self.connect_mat.shape[1]):
                if self.connect_mat[i][j] == 1:
                    point0_x_y = self.get_point_x_y(i)
                    point1_x_y = self.get_point_x_y(j)
                    cv2.line(img, point0_x_y, point1_x_y, (0, 255, 0))

        return img

    def get_connect_mat(self):
        connect_mat = np.zeros((self.world_size, self.world_size),dtype=np.uint16)
        #get connect_mat
        for i in range(self.world_size):
            for ii in range(self.connect_number):
                connect_mat[i, self.is_inside_world_size(i - 1 - ii)] = 1
                connect_mat[i, self.is_inside_world_size(i + 1 + ii)] = 1
        #random reconnect
        reconnect_mat = connect_mat.copy()
        for i in range(connect_mat.shape[0]):
            for j in range(connect_mat.shape[1]):
                if reconnect_mat[i][j] == 1 and random.random() < self.reconnect_rate / 2:
                    new_i, new_j = i, j
                    while reconnect_mat[new_i][new_j] == 1 or new_i == new_j:#已有连接或自链接
                        reconnect_id = random.randint(0, 1)
                        new_i = i if reconnect_id != 0 else random.randint(0, self.world_size - 1)
                        new_j = j if reconnect_id != 1 else random.randint(0, self.world_size - 1)
                    reconnect_mat[i][j] = 0
                    reconnect_mat[j][i] = 0
                    reconnect_mat[new_i][new_j] = 1
                    reconnect_mat[new_j][new_i] = 1
        return reconnect_mat

    def is_inside_world_size(self, i):
        if i >= self.world_size:
            i = i - self.world_size
        if i < 0:
            i = i + self.world_size
        return i

    def get_point_x_y(self, i):
        x = self.img_size[0] / 2 + self.img_r * math.cos(i / self.world_size * 2 * math.pi)
        y = self.img_size[0] / 2 + self.img_r * math.sin(i / self.world_size * 2 * math.pi)
        return int(x), int(y)

    def get_average_distance(self):
        distance_mat = np.ones((self.world_size, self.world_size), dtype=np.int16) * -1 + np.eye(self.world_size, dtype=np.uint16)
        for i in range(self.world_size):
            A_by_i = self.connect_mat.copy()
            for ii in range(i):
                A_by_i = A_by_i.dot(self.connect_mat)
            new_distance_locate = np.bitwise_and(np.where(A_by_i, 1, 0), np.where(distance_mat == -1, 1, 0))
            #print('i='+str(i))
            #print('np.where(A_by_i, 1, 0)')
            #print(np.where(A_by_i, 1, 0))
            #print('new_distance_locate')
            #print(new_distance_locate)
            distance_mat = distance_mat + new_distance_locate * (i + 2)
            #print('distance_locate')
            #print(distance_mat)
        #print('distance_mat')
        #print(distance_mat)
        #print(np.where(distance_mat >= 1, distance_mat, 0))
        #print(np.where(distance_mat >= 1, distance_mat, 0).sum())
        #print(np.where(distance_mat >= 1, 1, 0))
        #print(np.where(distance_mat >= 1, 1, 0).sum())
        return np.where(distance_mat >= 1, distance_mat, 0).sum() / np.where(distance_mat >= 1, 1, 0).sum()


if __name__ == '__main__':
    try_number = 50
    average_distances = 0
    for i in range(try_number):
        sw = small_world(100, 4, 0.15)
        #cv2.imshow('1', sw.get_img())
        #print(sw.connect_mat)
        #cv2.waitKey()
        average_distance = sw.get_average_distance()
        print(average_distance)
        average_distances = average_distances + average_distance
    print('result = ')
    print(average_distances / try_number)

    ####################
    #points, K, p
    #30, 4, 0,0, 4.137931034482761
    #30, 4, 0,01, 4.009057471264369
    #30, 4, 0,02, 3.783494252873564
    #30, 4, 0,05, 3.4653333333333323
    #30, 4, 0,1, 3.1538390804597696
    #30, 4, 0,15, 2.978666666666667
    #30, 4, 0,2, 2.823218390804597
    #30, 4, 0,3, 2.6872183908045972
    #30, 4, 0,4, 会出现网络破碎




