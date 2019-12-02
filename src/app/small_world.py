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
        self.connect_list = self.get_connection_list()

    def get_img(self):
        img = np.zeros(self.img_size, dtype=np.uint8)
        for i in range(self.world_size):
            point_x_y = self.get_point_x_y(i)
            img[point_x_y[0:2]] = (255, 0, 0)

        for i in range(len(self.connect_list)):
            point0_x_y = self.get_point_x_y(self.connect_list[i][0])
            point1_x_y = self.get_point_x_y(self.connect_list[i][1])
            cv2.line(img, point0_x_y, point1_x_y, (0, 255, 0))

        return img

    def get_connection_list(self):
        connection_list = []
        #get list
        for i in range(self.world_size):
            for ii in range(self.connect_number):
                new_connect = [i, self.is_inside_world_size(i - 1 - ii)]
                new_connect.sort()
                if new_connect not in connection_list:
                    connection_list.append(new_connect)
                new_connect = [i, self.is_inside_world_size(i + 1 + ii)]
                new_connect.sort()
                if new_connect not in connection_list:
                    connection_list.append(new_connect)
        #random reconnect
        for i in range(len(connection_list)):
            if random.random() < self.reconnect_rate:
                old_connect = connection_list[i]
                reconnect_id = random.randint(0, 1)
                new_connect = old_connect.copy()
                while new_connect in connection_list or new_connect[0] == new_connect[1]:
                    new_connect = old_connect.copy()
                    new_connect[reconnect_id] = random.randint(0, self.world_size)
                    new_connect.sort()
                connection_list[i] = new_connect
        return connection_list

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

    def get_distance(self, a, b):
        old_inside_list = [a]
        try_iter = 10000
        for distance in range(try_iter):
            new_inside_list = old_inside_list.copy()
            for i in range(len(self.connect_list)):
                for old_inside in old_inside_list:
                    if old_inside in self.connect_list[i]:
                        if self.connect_list[i][0] not in new_inside_list:
                            new_inside_list.append(self.connect_list[i][0])
                        if self.connect_list[i][1] not in new_inside_list:
                            new_inside_list.append(self.connect_list[i][1])
                        if b in new_inside_list:
                            return distance + 1
            old_inside_list = new_inside_list
        print('fail to connnect in %d iters,return iter' % try_iter)
        return try_iter

    def average_distance(self):
        pairs = 0
        distances = 0
        for a in range(self.world_size):
            for b in range(self.world_size):
                if a != b:
                    pairs = pairs + 1
                    distances = distances + self.get_distance(a, b)
        return distances / pairs


if __name__ == '__main__':
    try_number = 50
    average_distances = 0
    for i in range(try_number):
        sw = small_world(30, 4, 0.1)
        cv2.imshow('1', sw.get_img())
        cv2.waitKey()
        average_distance = sw.average_distance()
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




