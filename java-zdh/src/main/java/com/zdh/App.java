package com.zdh;

import com.zdh.algorithm.linked.Solution;
import com.zdh.algorithm.linked.node.ListNode;

/**
 * @author zdh
 * @date 2022-06-22 13:39
 * @Version 1.0
 */
public class App {
    public static void main(String[] args) {
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(8);
        ListNode node4 = new ListNode(2);
        ListNode node5 = new ListNode(5);
//        ListNode node6 = new ListNode(6);
//        ListNode node7 = new ListNode(7);
//        ListNode node8 = new ListNode(8);
        Solution solution = new Solution();
        solution.addNode(node2);
        solution.addNode(node3);
        solution.addNode(node4);
        solution.addNode(node5);
//        solution.addNode(node6);
//        solution.addNode(node7);
//        solution.addNode(node8);

        ListNode listNode = solution.showList();
//        solution.reorderList(listNode);
//        ListNode listNode1 = solution.sortList(listNode);
//        System.out.println(listNode1);
        System.out.println(solution.rotateRight(listNode,3));

    }
}
