package com.google.sps.servlets;

import com.google.protobuf.util.JsonFormat;
import com.google.sps.authentication.AuthenticationHandler;
import com.google.sps.data.OrderProto.Order;
import com.google.sps.dataManagers.OrderManager;
import com.google.sps.proto.FetchOrdersProto.FetchOrdersResponse;
import com.google.sps.proto.FetchOrdersProto.FetchOrdersRequest;
import com.google.sps.services.interfaces.FetchMyOrdersService;
import com.google.sps.proto.DeleteOrderProto.DeleteOrderResponse;
import com.google.sps.proto.DeleteOrderProto.DeleteOrderRequest;
import com.google.sps.services.interfaces.DeleteOrderService;

import java.io.BufferedReader;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet which retrieves all Orders made in the Room.
 */
@Singleton
public class MyOrderServlet extends HttpServlet {
    private FetchMyOrdersService fetchMyOrdersService;
    private DeleteOrderService deleteOrderService;
    private AuthenticationHandler authenticationHandler;

    @Inject
    public MyOrderServlet(FetchMyOrdersService fetchMyOrdersService, 
                            DeleteOrderService deleteOrderService,
                            AuthenticationHandler authenticationHandler) {
        this.fetchMyOrdersService = fetchMyOrdersService;
        this.deleteOrderService = deleteOrderService;
        this.authenticationHandler = authenticationHandler;
    }

    /**
     * Called by the server to allow this servlet to handle a GET request from the chatBox page.
     * @param request An HttpServletRequest object that contains the request the client has made of the servlet.
     * @param response An HttpServletResponse object that contains the response the servlet sends to the client.
     * @throws IOException If an input or output error is detected when the servlet handles the GET request.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!authenticationHandler.isUserLoggedIn()) {
            response.setStatus(400);
            return;
        }
        
        String roomId = request.getParameter("roomId");

        FetchOrdersRequest fetchOrdersRequest = FetchOrdersRequest.newBuilder().setRoomId(roomId).build();
        FetchOrdersResponse fetchOrdersResponse = fetchMyOrdersService.execute(fetchOrdersRequest);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JsonFormat.printer().print(fetchOrdersResponse));
        response.setStatus(200);
    }     
    
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!authenticationHandler.isUserLoggedIn()) {
            response.setStatus(400);
            return;
        }
        BufferedReader reader = request.getReader();
        String orderId = reader.readLine();
        orderId = orderId.split("=")[1];

        DeleteOrderRequest deleteOrderRequest = DeleteOrderRequest.newBuilder().setOrderId(orderId).build();
        DeleteOrderResponse deleteOrderResponse = deleteOrderService.execute(deleteOrderRequest);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JsonFormat.printer().print(deleteOrderResponse));
        response.setStatus(200);
    }
}
